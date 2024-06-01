### Server Meta

This "server" allows you to query available Minecraft Server versions.

Supports:

- Vanilla
- Forge
- Fabric
- Quilt
- NeoForge
- Paper


This API is hosted on https://servermeta.firstdark.dev/v1.

You can query the information with the following:

- Vanilla - `https://servermeta.firstdark.dev/v1/vanilla`
- Forge - `https://servermeta.firstdark.dev/v1/forge`
- Fabric - `https://servermeta.firstdark.dev/v1/fabric`
- Quilt - `https://servermeta.firstdark.dev/v1/quilt`
- NeoForge - `https://servermeta.firstdark.dev/v1/neoforge`
- Paper - `https://servermeta.firstdark.dev/v1/paper`


***

### Self Hosting

This api uses Docker to run. You can also build the application from scratch, if needed.

- Check out this repository to a folder on your Computer/Server
- Edit `docker-compose.yml` and replace `fdd-docker` with your network name
- Alternatively, configure an exposed port
- Run `docker compose up -d --build`


***

License:

The code of this application is licensed under the MIT license. You are allowed to use our hosted instance for your own applications as well, but you might be banned from it if you abuse it.