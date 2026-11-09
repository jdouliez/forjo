import { NgModule } from '@angular/core';
import { RouterModule, Routes, PreloadAllModules} from '@angular/router';
import { NotFoundComponent } from './not-found/not-found.component';
import { RemediationComponent } from './remediation/remediation.component';

const appRoutes: Routes = [
  {
    path: '',
    loadChildren: './bank/bank.module#BankModule'
  },
  {
    path: 'remediation',
    component: RemediationComponent
  },
  {
    path: '**',
    component: NotFoundComponent
  }
];
@NgModule({
  imports: [RouterModule.forRoot(appRoutes, { preloadingStrategy: PreloadAllModules})],
  exports: [RouterModule]
})
export class AppRoutingModule {

}
