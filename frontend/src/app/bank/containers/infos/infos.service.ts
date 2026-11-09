import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Infos } from './infos';

@Injectable({
  providedIn: 'root'
})
export class InfosService {
  private apiUrl = '/api/users/getinfos';

  constructor(private http: HttpClient) { }

  findAll(): Promise<Array<Infos>> {
    return this.http.get(`${this.apiUrl}`, {headers: new HttpHeaders({'Authorization' : 'Bearer ' + localStorage.getItem('token')})})
      .toPromise()
      .then(response => response as Infos[])
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<Array<any>> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
