import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CvService } from '../../core/services/cv.service';
import { AiCvGenerateRequest } from '../../core/models/cv.model';

@Component({
  selector: 'app-initial-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './initial-form.component.html',
  styleUrls: ['./initial-form.component.css']
})
export class InitialFormComponent {
  cvService = inject(CvService);

  formData: AiCvGenerateRequest = {
    fullName: '',
    email: '',
    phone: '',
    location: '',
    linkedinUrl: '',
    targetJobTitle: '',
    targetCountry: 'FR',
    rawProfileText: '',
    rawJobOfferText: ''
  };

  countries = [
    { code: 'FR', label: 'France (Photo acceptée)', isAnglo: false },
    { code: 'US', label: 'États-Unis (USA - Strictement sans photo)', isAnglo: true },
    { code: 'GB', label: 'Royaume-Uni (UK - Strictement sans photo)', isAnglo: true },
    { code: 'CA', label: 'Canada (Strictement sans photo)', isAnglo: true },
    { code: 'DE', label: 'Allemagne (Photo acceptée)', isAnglo: false }
  ];

  onSubmit(): void {
    if (this.formData.targetJobTitle.trim() && this.formData.rawProfileText.trim()) {
      this.cvService.generateCv(this.formData).subscribe();
    }
  }

  loadSampleData(): void {
    this.formData = {
      fullName: 'John Doe',
      email: 'john.doe@example.com',
      phone: '+33 6 12 34 56 78',
      location: 'Paris, France',
      linkedinUrl: 'https://linkedin.com/in/john-doe',
      targetJobTitle: 'Architecte Logiciel Backend Java & Cloud',
      targetCountry: 'FR',
      rawProfileText: `Ingénieur logiciel senior avec 8 ans d'expérience. 
Postes occupés :
- Lead Architecte chez CloudTech (2022 - Présent) : conception d'une plateforme de microservices traitant 5M de requêtes/jour sous Spring Boot et Kubernetes. Équipe de 6 développeurs.
- Développeur Backend Senior chez Digital Solutions (2019 - 2021) : migration de monolithes vers Spring Boot et PostgreSQL, indexation et réduction des latences de 50%.
Formations :
- Diplôme d'Ingénieur en Informatique à l'INSA (2019).
Compétences :
- Java 21, Spring Boot, Spring AI, PostgreSQL (JSONB), Docker, Kubernetes, CI/CD, DDD.`,
      rawJobOfferText: `Recherche Architecte Logiciel Java / Cloud pour piloter la refonte de nos services critiques.
Exigences :
- Maîtrise avancée de Spring Boot, Java moderne, API REST et PostgreSQL.
- Expérience concrète de la conteneurisation Docker / Kubernetes.
- Leadership technique et sens aigu de la qualité logicielle (tests unitaires, architecture résiliente).`
    };
  }

  isCurrentCountryAnglo(): boolean {
    return ['US', 'USA', 'GB', 'UK', 'CA'].includes(this.formData.targetCountry);
  }
}
