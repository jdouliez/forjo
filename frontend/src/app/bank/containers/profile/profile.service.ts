import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';


@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private apiUsersPath = '/api/users';
  private apiAccountCurrentUserPath = this.apiUsersPath + '/me';
  private apiClientsPath = '/api/clients';

  constructor(private http: HttpClient) { }

  getProfileInfos() : Promise<any> {
      return this.http.get(this.apiAccountCurrentUserPath, {responseType: 'json', headers: new HttpHeaders({'Authorization' : 'Bearer ' + localStorage.getItem('token')})})
          .toPromise()
          .then(response => {response as any; return response})
          .catch(this.handleError)
  }

  updateProfileInfos(data) : Promise<any> {
    return this.http.put(this.apiClientsPath, data, {responseType: 'json', headers: new HttpHeaders({'Authorization' : 'Bearer ' + localStorage.getItem('token')})})
        .toPromise()
        .then(response => {response as any; return response})
        .catch(this.handleError)
  }

  private handleError(error: any): Promise<Array<any>> {
    console.log("Erreur (" + error.status + ") --> " + error.error);
    return Promise.reject(error.message || error);
  }
}
