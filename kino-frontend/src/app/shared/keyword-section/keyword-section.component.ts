import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { KeywordDto } from '../../core/models/keyword-dto';

@Component({
  selector: 'app-keyword-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './keyword-section.component.html',
  styleUrl: './keyword-section.component.css'
})
export class KeywordSectionComponent {
  @Input({ required: true }) keywords: KeywordDto[] = [];
}
