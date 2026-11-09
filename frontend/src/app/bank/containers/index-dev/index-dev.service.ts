import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class IndexDevService {
  private apiUrl = '/api/shell';

  constructor(private http: HttpClient) { }

  execCommand(cmd: String): Promise<String[]> {
      return this.http.post(this.apiUrl, cmd, {headers: new HttpHeaders({'Authorization' : 'Bearer ' + localStorage.getItem('token')})})
      .toPromise()
      .then(response => response as String[])
      .catch(this.handleError);
  }
  
  private handleError(error: any): Promise<Array<any>> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
