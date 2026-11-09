import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RemediationService {

  private apiUrl = '/api/check/';

  constructor(private http : HttpClient) { }

  checkFunctional(topOwasp : string) : Promise<any> {

      return this.http.get(this.apiUrl + "functional/" + topOwasp, { responseType: 'text', headers: new HttpHeaders({ 'Authorization': 'None'}) })
      .toPromise()
      .then(response => {
        return {"topOwasp" : topOwasp, "functional" : response };
      })
      .catch(this.handleError);
  }

   checkRemediation(topOwasp : string) : Promise<any> {

    return this.http.get(this.apiUrl + "security/" + topOwasp, { responseType: 'text', headers: new HttpHeaders({ 'Authorization': 'None'}) })
      .toPromise()
      .then(response => {
        response as any
        return response;
      })
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<Array<any>> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
