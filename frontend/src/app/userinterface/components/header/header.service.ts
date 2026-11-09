import { HttpClient } from '@angular/common/http';
import { Injectable, NgZone } from '@angular/core';
import { Subject } from 'rxjs';
import { constantService } from '../../../constant.service';

@Injectable({
  providedIn: 'root'
})
export class HeaderService {

  private langUrl = '/api/lang';

  private static _zone: NgZone;
  private language: string = constantService.app.LANG_APP_DEFAULT;
  private static subject: Subject<any> = new Subject();
  private static loading: boolean = false;
  private static initialized: boolean = false;

  constructor(private zone: NgZone, private http: HttpClient) {
      HeaderService._zone = zone;
  }

  public static getZone(): NgZone {
      return this._zone;
  }

  public static getSubject(): Subject<any> {
      return this.subject;
  }

  public updateLanguage(lang: string) {
      HeaderService.loading = true;
      this.language = lang;
      setTimeout(() => {
          HeaderService.subject.next();
      }, 500);
  }

  private handleError(error: any): Promise<Array<any>> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
