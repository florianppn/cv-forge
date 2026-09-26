import { Component, Input, computed } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ats-gauge',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ats-gauge.component.html',
  styleUrls: ['./ats-gauge.component.css']
})
export class AtsGaugeComponent {
  @Input({ required: true }) score: number = 0;

  get strokeColor(): string {
    if (this.score >= 80) return '#10b981'; // Vert
    if (this.score >= 60) return '#f59e0b'; // Ambre
    return '#ef4444'; // Rouge
  }

  get strokeDashoffset(): number {
    const radius = 42;
    const circumference = 2 * Math.PI * radius;
    const progress = Math.min(Math.max(this.score, 0), 100) / 100;
    return circumference * (1 - progress);
  }

  get label(): string {
    if (this.score >= 80) return 'Excellente conformité ATS';
    if (this.score >= 60) return 'Conformité moyenne (améliorable)';
    return 'Faible compatibilité ATS';
  }
}
