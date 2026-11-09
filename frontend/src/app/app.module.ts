import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { UserinterfaceModule } from './userinterface/userinterface.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AppRoutingModule } from 'src/app/app-routing.module';
import { Router } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { NotFoundComponent } from './not-found/not-found.component';
import { RemediationComponent } from './remediation/remediation.component';
import { ChartsModule } from 'ng2-charts';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatProgressBarModule } from '@angular/material/progress-bar';


function _window(): any {
  // return the native window obj
  return window
}


@NgModule({
  declarations: [AppComponent, NotFoundComponent, RemediationComponent],
  imports: [BrowserModule,
    UserinterfaceModule,
    NgbModule.forRoot(),
    AppRoutingModule,
    HttpClientModule,
    MatProgressBarModule,
    FormsModule,
    ReactiveFormsModule,
    ChartsModule,
    BrowserModule,
    BrowserAnimationsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
    public language: any;
    public name: any;

    constructor() {
    }
}
