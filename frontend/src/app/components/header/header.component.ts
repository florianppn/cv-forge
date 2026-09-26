import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CvService } from '../../core/services/cv.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  cvService = inject(CvService);

  onSelectProfile(event: Event): void {
    const select = event.target as HTMLSelectElement;
    if (select.value) {
      this.cvService.selectProfile(select.value);
    }
  }

  onNewCv(): void {
    this.cvService.resetToNew();
  }

  onDownloadPdf(): void {
    this.cvService.downloadPdf();
  }
}
