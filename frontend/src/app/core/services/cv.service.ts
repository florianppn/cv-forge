import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { SessionService } from './session.service';
import {
  AiCvGenerateRequest,
  AiMatchRequest,
  CvContent,
  CvProfileResponse,
  CvStyleUpdateRequest,
  MatchResponse
} from '../models/cv.model';
import { catchError, tap } from 'rxjs/operators';
import { of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CvService {
  private http = inject(HttpClient);
  private sessionService = inject(SessionService);

  private readonly API_URL = 'http://localhost:8080/api/v1/cv';

  // Signals réactifs pour l'état de l'application
  currentProfile = signal<CvProfileResponse | null>(null);
  profilesList = signal<CvProfileResponse[]>([]);
  isGenerating = signal<boolean>(false);
  isMatching = signal<boolean>(false);
  isExporting = signal<boolean>(false);
  error = signal<string | null>(null);

  // Valeurs dérivées calculées
  hasProfile = computed(() => this.currentProfile() !== null);
  matchResult = computed(() => this.currentProfile()?.content?.matchResult ?? null);

  constructor() {
    this.loadSessionProfiles();
  }

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'X-Session-Id': this.sessionService.getSessionId()
    });
  }

  loadSessionProfiles(): void {
    const sessionId = this.sessionService.getSessionId();
    this.http.get<CvProfileResponse[]>(`${this.API_URL}?sessionId=${sessionId}`, { headers: this.getHeaders() })
      .pipe(
        catchError(err => {
          console.warn('Impossible de charger la liste des profils de session', err);
          return of([]);
        })
      )
      .subscribe(profiles => {
        this.profilesList.set(profiles);
        if (profiles.length > 0 && !this.currentProfile()) {
          this.currentProfile.set(profiles[0]);
        }
      });
  }

  generateCv(request: AiCvGenerateRequest) {
    this.isGenerating.set(true);
    this.error.set(null);
    request.sessionId = this.sessionService.getSessionId();

    return this.http.post<CvProfileResponse>(`${this.API_URL}/generate`, request, { headers: this.getHeaders() })
      .pipe(
        tap(profile => {
          this.currentProfile.set(profile);
          this.isGenerating.set(false);
          this.loadSessionProfiles();
        }),
        catchError(err => {
          this.isGenerating.set(false);
          const detail = err.error?.detail || 'Erreur lors de la génération du CV par l\'IA.';
          this.error.set(detail);
          throw err;
        })
      );
  }

  selectProfile(id: string): void {
    const found = this.profilesList().find(p => p.id === id);
    if (found) {
      this.currentProfile.set(found);
    } else {
      this.http.get<CvProfileResponse>(`${this.API_URL}/${id}`, { headers: this.getHeaders() })
        .subscribe(profile => this.currentProfile.set(profile));
    }
  }

  updateContent(newContent: CvContent) {
    const profile = this.currentProfile();
    if (!profile) return;

    // Mise à jour optimiste immédiate dans le signal pour zéro latence
    this.currentProfile.update(curr => curr ? { ...curr, content: newContent } : null);

    this.http.put<CvProfileResponse>(`${this.API_URL}/${profile.id}/content`, { content: newContent }, { headers: this.getHeaders() })
      .subscribe({
        next: updated => this.currentProfile.set(updated),
        error: err => console.error('Erreur mise à jour contenu', err)
      });
  }

  updateStyle(styleUpdate: CvStyleUpdateRequest) {
    const profile = this.currentProfile();
    if (!profile) return;

    // Mise à jour optimiste locale immédiate pour la réactivité de l'aperçu A4
    this.currentProfile.update(curr => {
      if (!curr) return null;
      return {
        ...curr,
        style: {
          ...curr.style,
          ...styleUpdate
        }
      };
    });

    this.http.put<CvProfileResponse>(`${this.API_URL}/${profile.id}/style`, styleUpdate, { headers: this.getHeaders() })
      .subscribe({
        next: updated => this.currentProfile.set(updated),
        error: err => console.error('Erreur mise à jour style', err)
      });
  }

  matchWithJobOffer(jobOfferText: string) {
    const profile = this.currentProfile();
    if (!profile) return;

    this.isMatching.set(true);
    this.error.set(null);

    const body: AiMatchRequest = { jobOfferText };
    this.http.post<MatchResponse>(`${this.API_URL}/${profile.id}/match`, body, { headers: this.getHeaders() })
      .pipe(
        tap(res => {
          this.isMatching.set(false);
          this.currentProfile.update(curr => {
            if (!curr) return null;
            return {
              ...curr,
              content: {
                ...curr.content,
                matchResult: res.matchResult
              }
            };
          });
        }),
        catchError(err => {
          this.isMatching.set(false);
          const detail = err.error?.detail || 'Erreur lors de l\'analyse de matching ATS.';
          this.error.set(detail);
          throw err;
        })
      ).subscribe();
  }

  uploadPhoto(file: File) {
    const profile = this.currentProfile();
    if (!profile) return;

    const formData = new FormData();
    formData.append('file', file);

    this.http.post<CvProfileResponse>(`${this.API_URL}/${profile.id}/photo`, formData, { headers: this.getHeaders() })
      .subscribe({
        next: updated => this.currentProfile.set(updated),
        error: err => {
          console.error('Erreur upload photo', err);
          this.error.set(err.error?.detail || 'Format ou taille de photo non valide (max 2 Mo).');
        }
      });
  }

  downloadPdf(): void {
    const profile = this.currentProfile();
    if (!profile) return;

    this.isExporting.set(true);
    this.http.get(`${this.API_URL}/${profile.id}/pdf`, {
      headers: this.getHeaders(),
      responseType: 'blob'
    }).subscribe({
      next: blob => {
        this.isExporting.set(false);
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        const candidateName = profile.content?.contactInfo?.fullName?.replace(/\s+/g, '_') || 'CV';
        a.download = `${candidateName}_ATS_Resume.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },
      error: err => {
        this.isExporting.set(false);
        console.error('Erreur téléchargement PDF', err);
        this.error.set('Impossible de générer le document PDF final.');
      }
    });
  }

  resetToNew(): void {
    this.currentProfile.set(null);
  }
}
