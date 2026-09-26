import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CvService } from '../../core/services/cv.service';
import { EditorPanelComponent } from './editor-panel/editor-panel.component';
import { PreviewSheetComponent } from './preview-sheet/preview-sheet.component';

@Component({
  selector: 'app-studio',
  standalone: true,
  imports: [CommonModule, EditorPanelComponent, PreviewSheetComponent],
  templateUrl: './studio.component.html',
  styleUrls: ['./studio.component.css']
})
export class StudioComponent {
  cvService = inject(CvService);
}
