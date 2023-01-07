import { registerPlugin } from '@capacitor/core';

import type { EsptouchActivityPlugin } from './definitions';

const EsptouchActivity = registerPlugin<EsptouchActivityPlugin>(
  'EsptouchActivity',
  {
    web: () => import('./web').then(m => new m.EsptouchActivityWeb()),
  },
);

export * from './definitions';
export { EsptouchActivity };
