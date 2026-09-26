import { Component, Input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CvProfileResponse } from '../../../core/models/cv.model';

@Component({
  selector: 'app-preview-sheet',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './preview-sheet.component.html',
  styleUrls: ['./preview-sheet.component.css']
})
export class PreviewSheetComponent {
  @Input({ required: true }) profile!: CvProfileResponse;

  zoomLevel = signal<number>(100);

  setZoom(level: number): void {
    this.zoomLevel.set(level);
  }

  get fontFamilyCss(): string {
    const font = this.profile?.style?.fontFamily || 'Helvetica';
    if (font === 'Times New Roman') return "'Times New Roman', Times, serif";
    if (font === 'Garamond') return "Garamond, Baskerville, 'Hoefler Text', serif";
    if (font === 'Arial') return "Arial, 'Helvetica Neue', sans-serif";
    return "Helvetica, Arial, sans-serif";
  }

  get primaryColor(): string {
    return this.profile?.style?.primaryColor || '#1a365d';
  }

  get densityClass(): string {
    const density = this.profile?.style?.density || 'NORMAL';
    return `density-${density.toLowerCase()}`;
  }

  get bulletClass(): string {
    const bullet = this.profile?.style?.bulletStyle || 'DISC';
    return `bullet-${bullet.toLowerCase()}`;
  }

  get sectionOrder(): string[] {
    return this.profile?.style?.sectionOrder || ['SUMMARY', 'EXPERIENCE', 'EDUCATION', 'SKILLS'];
  }
}
