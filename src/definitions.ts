export interface EsptouchActivityPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
