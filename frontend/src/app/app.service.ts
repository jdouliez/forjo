import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  private apiUrl = '/api/users';
  
  constructor(private http: HttpClient) { }
  
  verify(token: string): Promise<any> {
    if (token) {
      return this.http.get(this.apiUrl, { headers: new HttpHeaders({ 'Authorization': 'Bearer ' + token }) })
        .toPromise()
        .then(response => response as any)
        .catch(err => err as any);
    }
    else {
       return new Promise(
        function (resolve) {
            resolve({ "status": 401});
        });
  }
}

  private handleError(error: any): Promise<Array<any>> {
    console.error('An error occurred', error);
    return Promise.reject(error.mesage || error);
  }
}
