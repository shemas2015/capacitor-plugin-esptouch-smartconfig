# Requirements
Capacitor 4

# esptouch-smartconfig-plugin

Ionic capacitor plugin to connect esp devices with smart config

## Install

```bash
npm install https://github.com/shemas2015/capacitor-plugin-esptouch-smartconfig.git
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`connect(...)`](#connect)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### connect(...)

```typescript
connect(options: Iconnect) => Promise<Iconnect>
```

| Param         | Type                                          |
| ------------- | --------------------------------------------- |
| **`options`** | <code><a href="#iconnect">Iconnect</a></code> |

**Returns:** <code>Promise&lt;<a href="#iconnect">Iconnect</a>&gt;</code>

--------------------


### Interfaces


#### Iconnect

| Prop           | Type                |
| -------------- | ------------------- |
| **`ip`**       | <code>string</code> |
| **`ssid`**     | <code>string</code> |
| **`password`** | <code>string</code> |

</docgen-api>

# Works example
## Import
```
import { EsptouchActivity } from 'esptouch-smartconfig-plugin';
```
## Use
```
const conection = {
      ip      : "Android Device Ip",
      ssid    : "SSID name - Wifi Network name",
      password: "Wifi password",
    }
    this.disabled = true;

    EsptouchActivity.connect(conection).then( (result:any) => {
      this.disabled = false ;
      console.log("resultado: ",result);
    }).catch(error => {
      this.disabled = false ;
      console.log( "error" , error );
    })
```
