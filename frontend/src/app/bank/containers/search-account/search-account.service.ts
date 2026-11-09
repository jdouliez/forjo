import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { SearchAccount } from './search-account';

@Injectable({
  providedIn: 'root'
})
export class SearchAccountService {
  private apiUrl = '/api/pdf/search';

  constructor(private http: HttpClient) { }


  searchAccount(id: string): Promise<Array<SearchAccount>> {
    return this.http.get(`${this.apiUrl}/${id}`, {headers: new HttpHeaders({'Authorization' : 'Bearer ' + localStorage.getItem('token')})})
      .toPromise()
      .then(response => response as SearchAccount[])
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<Array<any>> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
