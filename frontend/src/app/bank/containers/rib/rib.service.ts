import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { saveAs } from 'file-saver'

@Injectable({
  providedIn: 'root'
})
export class RibService {
  private apiUrl = '/api/downloadFile';
  private apiAccountUrl = '/api/account/rib';
  
  constructor(private http: HttpClient) { }
  
  getCompteCourant() : Promise<any> {
      return this.http.get(this.apiAccountUrl, {responseType: 'text', headers: new HttpHeaders({'Authorization' : 'Bearer ' + localStorage.getItem('token')})})
          .toPromise()
          .then(response => {response as any; return response})
          .catch(this.handleError)
  }
  
  downloadRib(file: string) {
      var param = "filename=" + file;
      this.http.post(`${this.apiUrl}`, param, {headers: new HttpHeaders({'Content-type': 'application/x-www-form-urlencoded', 'Authorization' :  'Bearer ' + localStorage.getItem('token'), 'Accept': 'application/pdf'}), 'responseType' : 'blob'})
          .toPromise()
          .then(response => {
              saveAs(response, file)
          })
          .catch(this.handleError);
  }

  private handleError(error: any): Promise<Array<any>> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
