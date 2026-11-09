import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdvisorComponent } from './containers/advisor/advisor.component';
import { InfosComponent } from './containers/infos/infos.component';
import { AccountComponent } from './containers/account/account.component';
import { RibComponent } from './containers/rib/rib.component';
import { BankRoutingModule } from './bank-routing.module';
import { IndexComponent } from './containers/index/index.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AddAccountComponent } from './containers/add-account/add-account.component';
import { SearchAccountComponent } from './containers/search-account/search-account.component';
import { IndexDevComponent } from './containers/index-dev/index-dev.component';
import { AdminComponent } from './containers/admin/admin.component';
import { AdminMessageComponent } from './containers/admin-message/admin-message.component';
import { AdminHoneyComponent } from './containers/admin-honey/admin-honey.component';
import { AccountInfoComponent } from "./containers/account-info/account-info.component";
import { ProfileComponent } from './containers/profile/profile.component';
import { MySafeComponent } from './containers/my-safe/my-safe.component';

@NgModule({
  declarations: [AdvisorComponent, InfosComponent, AccountComponent, AccountInfoComponent, RibComponent, IndexComponent, AddAccountComponent, SearchAccountComponent, IndexDevComponent, AdminComponent, AdminMessageComponent, AdminHoneyComponent, ProfileComponent, MySafeComponent],
  imports: [
    CommonModule,
    BankRoutingModule,
    NgbModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [],
})
export class BankModule { }
