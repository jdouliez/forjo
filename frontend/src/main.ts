import { enableProdMode, NgZone, MissingTranslationStrategy } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import { environment } from './environments/environment';

import { getTranslationProviders } from './TranslationProvider';
import { HeaderService } from './app/userinterface/components/header/header.service';
import { Subject } from 'rxjs';
import { constantService } from './app/constant.service';

if (environment.production) {
  enableProdMode();
}

function translate() {
    getTranslationProviders().then(providers => {
        const options = {
            missingTranslation: MissingTranslationStrategy.Ignore,
            providers
        };
        platformBrowserDynamic().bootstrapModule(AppModule, options);
    })
}

HeaderService.getSubject().subscribe(() => {
    if (HeaderService.getZone()) {
        HeaderService.getZone().runOutsideAngular(() => {
            translate();
        })
    } else {
        translate();
    }
})

HeaderService.getSubject().next();