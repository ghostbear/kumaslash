## NPM
1. Copy and rename `.env.example` to `.env`
2. Replace the example values in `.env` with your Discord bot token and optinally guild id
3. `npm install`
4. `npm run build`
5. `npm run serve`

## Docker
```
docker run --env TOKEN=<YOUR_TOKEN> --env GUILD_ID=<YOUR_GUILD_ID> -d ghcr.io/ghostbear/kumaslash:latest
```
