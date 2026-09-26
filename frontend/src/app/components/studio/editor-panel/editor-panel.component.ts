import { Component, Input, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CvService } from '../../../core/services/cv.service';
import {
  CvProfileResponse,
  CvContent,
  Experience,
  Education,
  SkillCategory,
  Density,
  BulletStyle
} from '../../../core/models/cv.model';
import { AtsGaugeComponent } from '../ats-gauge/ats-gauge.component';

@Component({
  selector: 'app-editor-panel',
  standalone: true,
  imports: [CommonModule, FormsModule, AtsGaugeComponent],
  templateUrl: './editor-panel.component.html',
  styleUrls: ['./editor-panel.component.css']
})
export class EditorPanelComponent {
  @Input({ required: true }) profile!: CvProfileResponse;

  cvService = inject(CvService);

  activeTab = signal<'content' | 'style' | 'matching'>('content');
  jobOfferInput = signal<string>('');

  presetColors = [
    { label: 'Bleu Marine (Navy)', value: '#1a365d' },
    { label: 'Ardoise Sombre', value: '#0f172a' },
    { label: 'Vert Émeraude', value: '#0f766e' },
    { label: 'Bordeaux Exécutif', value: '#881337' },
    { label: 'Indigo Profond', value: '#3730a3' },
    { label: 'Charbon Neutre', value: '#334155' }
  ];

  fontOptions = [
    { label: 'Helvetica / Sans-Serif (Standard ATS)', value: 'Helvetica' },
    { label: 'Arial (Classique et lisible)', value: 'Arial' },
    { label: 'Times New Roman (Traditionnel Serif)', value: 'Times New Roman' },
    { label: 'Garamond (Élégant Serif)', value: 'Garamond' }
  ];

  // Gestion du style
  onColorChange(color: string): void {
    this.cvService.updateStyle({ primaryColor: color });
  }

  onFontChange(font: string): void {
    this.cvService.updateStyle({ fontFamily: font });
  }

  onDensityChange(density: Density): void {
    this.cvService.updateStyle({ density });
  }

  onBulletChange(bullet: BulletStyle): void {
    this.cvService.updateStyle({ bulletStyle: bullet });
  }

  moveSectionUp(index: number): void {
    const order = [...this.profile.style.sectionOrder];
    if (index > 0) {
      const temp = order[index];
      order[index] = order[index - 1];
      order[index - 1] = temp;
      this.cvService.updateStyle({ sectionOrder: order });
    }
  }

  moveSectionDown(index: number): void {
    const order = [...this.profile.style.sectionOrder];
    if (index < order.length - 1) {
      const temp = order[index];
      order[index] = order[index + 1];
      order[index + 1] = temp;
      this.cvService.updateStyle({ sectionOrder: order });
    }
  }

  onPhotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.cvService.uploadPhoto(input.files[0]);
    }
  }

  // Gestion du contenu
  onContentChange(): void {
    this.cvService.updateContent(this.profile.content);
  }

  addExperience(): void {
    const newExp: Experience = {
      title: 'Nouveau Poste',
      company: 'Entreprise',
      startDate: '2024',
      currentJob: true,
      bullets: ['Accomplissement clé formulé avec un verbe d\'action dynamique.']
    };
    this.profile.content.experiences.unshift(newExp);
    this.onContentChange();
  }

  removeExperience(index: number): void {
    this.profile.content.experiences.splice(index, 1);
    this.onContentChange();
  }

  addBullet(exp: Experience): void {
    exp.bullets.push('Nouvelle réalisation chiffrée avec résultats mesurables.');
    this.onContentChange();
  }

  removeBullet(exp: Experience, index: number): void {
    exp.bullets.splice(index, 1);
    this.onContentChange();
  }

  addEducation(): void {
    const newEdu: Education = {
      degree: 'Intitulé du Diplôme',
      institution: 'Établissement ou Université',
      graduationDate: '2023'
    };
    this.profile.content.educations.push(newEdu);
    this.onContentChange();
  }

  removeEducation(index: number): void {
    this.profile.content.educations.splice(index, 1);
    this.onContentChange();
  }

  addSkillCategory(): void {
    const newCat: SkillCategory = {
      categoryName: 'Nouvelle Catégorie',
      skills: ['Compétence 1', 'Compétence 2']
    };
    this.profile.content.skills.push(newCat);
    this.onContentChange();
  }

  removeSkillCategory(index: number): void {
    this.profile.content.skills.splice(index, 1);
    this.onContentChange();
  }

  // Matching ATS
  runMatching(): void {
    if (this.jobOfferInput().trim()) {
      this.cvService.matchWithJobOffer(this.jobOfferInput().trim());
    }
  }

  addMissingKeywordToSkills(keyword: string): void {
    if (!this.profile.content.skills || this.profile.content.skills.length === 0) {
      this.profile.content.skills = [{ categoryName: 'Compétences Requises', skills: [keyword] }];
    } else {
      this.profile.content.skills[0].skills.push(keyword);
    }
    // Retirer de la liste manquante visuellement
    if (this.profile.content.matchResult) {
      this.profile.content.matchResult.missingKeywords =
        this.profile.content.matchResult.missingKeywords.filter(k => k !== keyword);
      this.profile.content.matchResult.matchedKeywords.push(keyword);
    }
    this.onContentChange();
  }

  trackByIndex(index: number): number {
    return index;
  }

  getSectionTitle(sec: string): string {
    switch (sec) {
      case 'SUMMARY': return 'Profil Professionnel';
      case 'EXPERIENCE': return 'Expérience Professionnelle';
      case 'EDUCATION': return 'Formation & Diplômes';
      case 'SKILLS': return 'Compétences Clés';
      default: return sec;
    }
  }
}
