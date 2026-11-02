import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

import enTranslation from './locales/en.json';
import taTranslation from './locales/ta.json';

const resources = {
  en: {
    translation: enTranslation
  },
  ta: {
    translation: taTranslation
  }
};

const savedLang = localStorage.getItem('preferred_language') || 'en';

i18n
  .use(initReactI18next)
  .init({
    resources,
    lng: savedLang,
    fallbackLng: 'en',
    interpolation: {
      escapeValue: false
    }
  });

export default i18n;

