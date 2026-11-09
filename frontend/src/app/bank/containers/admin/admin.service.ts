import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class AdminService {
    private apiUrl = '/api/admin'

    constructor(private http: HttpClient) { }

    admin(): Promise<any>  {
      return this.http.get(this.apiUrl, { responseType: 'text', headers: new HttpHeaders({ 'Authorization': 'Bearer ' + localStorage.getItem('token') }) })
        .toPromise()
        .then(response => response as any)
        .catch(this.handleError);
    }

    private handleError(error: any): Promise<Array<any>> {
        console.error('An error occurred', error);
        return Promise.reject(error.mesage || error);
    }
}
