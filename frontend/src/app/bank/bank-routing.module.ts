import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AccountComponent } from 'src/app/bank/containers/account/account.component';
import { AdvisorComponent } from 'src/app/bank/containers/advisor/advisor.component';
import { InfosComponent } from 'src/app/bank/containers/infos/infos.component';
import { RibComponent } from 'src/app/bank/containers/rib/rib.component';
import { IndexComponent } from 'src/app/bank/containers/index/index.component';
import { SearchAccountComponent } from 'src/app/bank/containers/search-account/search-account.component';
import { AddAccountComponent } from 'src/app/bank/containers/add-account/add-account.component';
import { IndexDevComponent } from 'src/app/bank/containers/index-dev/index-dev.component';
import { AdminComponent } from 'src/app/bank/containers/admin/admin.component'
import { AdminMessageComponent } from 'src/app/bank/containers/admin-message/admin-message.component';
import { AdminHoneyComponent } from 'src/app/bank/containers/admin-honey/admin-honey.component';
import { AccountInfoComponent } from './containers/account-info/account-info.component';
import { ProfileComponent } from './containers/profile/profile.component';
import { MySafeComponent } from './containers/my-safe/my-safe.component';

const appRoutes: Routes = [
  { path: '', component: IndexComponent },
  { path: 'index', component: IndexComponent },
  { path: 'nutshell', component: IndexDevComponent },
  { path: 'account', component: AccountComponent },
  { path: 'account/add', component: AddAccountComponent },
  { path: 'account/:id', component: AccountInfoComponent },
  { path: 'account/search/:id', component: SearchAccountComponent },
  { path: 'advisor/:id', component: AdvisorComponent },
  { path: 'infos', component: InfosComponent },
  { path: 'rib', component: RibComponent },
  { path: 'admin', component: AdminComponent },
  { path: 'admin/view/message', component: AdminMessageComponent },
  { path: 'honeylog', component: AdminHoneyComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'safe', component: MySafeComponent }
];

@NgModule({
  imports: [RouterModule.forChild(appRoutes)],
  exports: [RouterModule]
})
export class BankRoutingModule { }
