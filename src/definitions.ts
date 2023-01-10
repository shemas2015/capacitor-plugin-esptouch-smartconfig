export interface Iconnect{
  ip      :string;
  ssid    : string;
  password: string;
}
export interface EsptouchActivityPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  connect(options: Iconnect): Promise<Iconnect>;
}
