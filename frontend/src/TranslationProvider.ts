import { TRANSLATIONS, TRANSLATIONS_FORMAT, LOCALE_ID } from '@angular/core';
import { constantService } from './app/constant.service';

declare const require: any;
//
//declare var System: any;

export function getTranslationProviders(): Promise<any[]> {
    const localeValue = localStorage.getItem("local") ? localStorage.getItem("local") : constantService.app.LANG_APP_DEFAULT;
    localStorage.setItem("local", localeValue);
    const noProviders: Object[] = [];
    if (localeValue === constantService.app.LANG_APP_DEFAULT) {
        return Promise.resolve(noProviders);
    }
    try {
        const translations = require(`raw-loader!./locale/messages.${localeValue}.xlf`);
        return Promise.resolve([
            { provide: TRANSLATIONS, useValue: translations },
            { provide: TRANSLATIONS_FORMAT, useValue: 'xlf' },
            { provide: LOCALE_ID, useValue: localeValue }
        ]);
    } catch (error) {
        console.error(error);
        return Promise.resolve(noProviders);
    }
}
