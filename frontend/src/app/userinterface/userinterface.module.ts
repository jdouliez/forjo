import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FooterComponent } from './components/footer/footer.component';
import { HeaderComponent } from './components/header/header.component';
import { NavComponent } from './components/nav/nav.component';
import { UserinterfaceComponent } from './containers/userinterface/userinterface.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
//import { NotFoundComponent } from './components/not-found/not-found.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    NgbModule
  ],
  declarations: [UserinterfaceComponent, NavComponent, HeaderComponent, FooterComponent],//, NotFoundComponent],
  exports : [ UserinterfaceComponent]

})
export class UserinterfaceModule { }
