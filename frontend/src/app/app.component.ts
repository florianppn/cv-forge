import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CvService } from './core/services/cv.service';
import { HeaderComponent } from './components/header/header.component';
import { InitialFormComponent } from './components/initial-form/initial-form.component';
import { StudioComponent } from './components/studio/studio.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    HeaderComponent,
    InitialFormComponent,
    StudioComponent
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  cvService = inject(CvService);
}
