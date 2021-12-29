# kumaslash

## Slash commands

### Source
Find source by `ID`

`ID` - Source id

```
/source id:<ID>
```

### Download
Get download link by `TYPE`

`TYPE` - `TACHIYOMI` / `TACHIYOMI-SY` / `TACHIYOMI-J2K` / `NEKO`

```
/download type:<TYPE>
```


## NPM
1. Copy and rename `.env.example` to `.env`
2. Replace the example values in `.env` with your Discord bot token and optinally guild id
3. `npm install`
4. `npm run build`
5. `npm run serve`

## Docker
Powershell
```powershell
docker run  `
  --env TOKEN=<YOUR_TOKEN> `
  --env GUILD_ID=<YOUR_GUILD_ID> `
  --env SUPPORT_ID=<SUPPORT_ROLE_ID> `
  -d ghcr.io/ghostbear/kumaslash:latest
```

Unix
```bash
docker run  \
  --env TOKEN=<YOUR_TOKEN> \
  --env GUILD_ID=<YOUR_GUILD_ID> \
  --env SUPPORT_ID=<SUPPORT_ROLE_ID> \
  -d ghcr.io/ghostbear/kumaslash:latest
```
