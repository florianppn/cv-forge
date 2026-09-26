import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private readonly SESSION_KEY = 'cvforge_session_id';

  getSessionId(): string {
    let sessionId = localStorage.getItem(this.SESSION_KEY);
    if (!sessionId) {
      sessionId = crypto.randomUUID();
      localStorage.setItem(this.SESSION_KEY, sessionId);
    }
    return sessionId;
  }
}
