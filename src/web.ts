import { WebPlugin } from '@capacitor/core';

import type { EsptouchActivityPlugin } from './definitions';

export class EsptouchActivityWeb
  extends WebPlugin
  implements EsptouchActivityPlugin
{
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
