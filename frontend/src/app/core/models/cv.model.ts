export interface ContactInfo {
  fullName?: string;
  email?: string;
  phone?: string;
  location?: string;
  linkedinUrl?: string;
  portfolioUrl?: string;
}

export interface Experience {
  title: string;
  company: string;
  location?: string;
  startDate: string;
  endDate?: string;
  currentJob: boolean;
  bullets: string[];
}

export interface Education {
  degree: string;
  institution: string;
  location?: string;
  startDate?: string;
  graduationDate?: string;
  details?: string;
}

export interface SkillCategory {
  categoryName: string;
  skills: string[];
}

export interface MatchResult {
  targetJobTitle?: string;
  matchScore: number;
  matchedKeywords: string[];
  missingKeywords: string[];
  strengths: string[];
  suggestions: string[];
}

export interface CvContent {
  contactInfo?: ContactInfo;
  professionalSummary?: string;
  experiences: Experience[];
  educations: Education[];
  skills: SkillCategory[];
  matchResult?: MatchResult;
}

export type Density = 'COMPACT' | 'NORMAL' | 'SPACIOUS';
export type BulletStyle = 'DISC' | 'CIRCLE' | 'SQUARE' | 'HYPHEN';

export interface CvStyle {
  primaryColor: string;
  fontFamily: string;
  density: Density;
  bulletStyle: BulletStyle;
  sectionOrder: string[];
  showPhoto: boolean;
}

export interface CvProfileResponse {
  id: string;
  sessionId: string;
  userEmail?: string;
  targetCountry?: string;
  photoUrl?: string;
  content: CvContent;
  style: CvStyle;
  createdAt: string;
  updatedAt: string;
}

export interface AiCvGenerateRequest {
  fullName?: string;
  email?: string;
  phone?: string;
  location?: string;
  linkedinUrl?: string;
  portfolioUrl?: string;
  targetJobTitle: string;
  rawProfileText: string;
  rawJobOfferText?: string;
  targetCountry: string;
  sessionId?: string;
}

export interface CvStyleUpdateRequest {
  primaryColor?: string;
  fontFamily?: string;
  density?: Density;
  bulletStyle?: BulletStyle;
  sectionOrder?: string[];
  showPhoto?: boolean;
}

export interface AiMatchRequest {
  jobOfferText: string;
}

export interface MatchResponse {
  matchResult: MatchResult;
}
