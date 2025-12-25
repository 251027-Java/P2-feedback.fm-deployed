# Jenkins

This guide covers how to set up Jenkins locally.

## Prequisites

- [Docker](https://www.docker.com/get-started/)
- [Just](https://just.systems/man/en/packages.html)
  
  An easy way to install Just is using `npm`:

  ```sh
  npm i -g rust-just
  just --version # verify that Just was installed
  ```

  Just can be installed through other [package managers](https://just.systems/man/en/packages.html) and is also available as [pre-built binaries](https://just.systems/man/en/pre-built-binaries.html).

## Using `just`

`just` commands/recipes are defined in the [justfile](./justfile). 

To view all available recipes:

```sh
just -l
```

To run commands, you'll need a `.env` file. See [this section](#initialization) for more information.

## Initialization

### `.env` Configuration

In order to run, an `.env` file must be present and configured. 

Copy the example `.env`:

```sh
cp .env.example .env
```

Some variables already have provided values. These values can be modified to better suit your configuration if necessary.

In order to use Jenkins, an account is necessary. This account information is local to the Jenkins instance, and the account will be created upon initialization. Specify values for `JENKINS_USER` and `JENKINS_PASSWORD` to set the username and password, respectively.

### Creation

Once set, the Jenkins server can be created:

```sh
just init
```

This will create the Jenkins server with the necessary plugins installed and minimal configuration. After running this command, there will be additional instructions output to the terminal that should be completed to finish the set up which will briefly be discussed in this guide. If you want to see those instructions at any point, use `just init-text`.

### Jenkins API Token and Jenkins CLI

Using the Jenkins CLI allows for remote use, scriptability, and interaction with the Jenkins server through the terminal. In order to send commands to a Jenkins server, you need an API token. A password could also be used rather than an API token, but for security management, it's recommended to use an API token as multiple can be made and also be deleted if necessary.

To create an API token for a user, go to `<jenkins-url>/user/<username>/security` in your browser where `<jenkins-url>` is the URL to the Jenkins server and `<username>` is the name of the user, e.g. `http://localhost:8080/user/admin/security`. After creating the token, copy the token and set the `JENKINS_API_TOKEN` variable in the `.env` file to the token.

If you haven't downloaded the CLI, use `just get-cli`. Once you have the CLI, you should be able to use the Jenkins CLI through `just jc`.

```sh
just jc list-jobs
just jc version
```

Use `just help` to view available commands, or in your browser, go to `<jenkins-url>/cli`. Substitute `<jenkins-url>` with the URL of your Jenkins server. 

### GitHub Rate Limiting

In order for Jenkins to retrieve our code or interact with GitHub, the server will send API requests to GitHub. GitHub has [limits](https://docs.github.com/en/rest/using-the-rest-api/rate-limits-for-the-rest-api?apiVersion=2022-11-28#about-primary-rate-limits) for unauthenticated and authenticated users. Notably, unauthenticated requests are limited to only 60 requests per hour, which impact the running of our pipelines if we remain unauthenticated. 

To obtain a higher limit, we can use [personal access tokens (PATs)](https://github.com/settings/personal-access-tokens) from GitHub. After creating a token, provide your username and the token as credentials in Jenkins.

An example file is provided in the `creds/` directory. Copy the file:

```sh
cp creds/github-pat.xml.example creds/github-pat.xml
```

Replace the placeholder content for the username and password elements with your GitHub username and the PAT. If following the instructions from `just init`, these credentials will be imported to Jenkins with `just post-init`. They can also be manually imported with `just import-creds`.

### Importing/Exporting Jobs

To share Jenkins jobs across multiple computers or users, we can export and import them using the Jenkins CLI. Existing jobs for our server can found in the `jobs/` directory. These will be imported when running `just post-init` or can be manually ran with `just import-jobs`.

```sh
just export-jobs # Job configs will be output to the jobs/ directory
just import-jobs # Import jobs from the jobs/ directory
```

## Starting

Start Jenkins:

```sh
just start
```

## Stopping

Stop Jenkins:

```sh
just stop
```

## Destroying

To destroy all of Jenkins, including its data, run:

```sh
just destroy
```

## Tunneling

To test webhook interactions with our pipeline on our local environment, we can use a tunnel to redirect payloads to our server without having to expose our own ports.

To enable this, ensure the Jenkins server is running and then run:

```sh
just tunnel # Outputs a URL where we can access our Jenkins server
```

This uses [localtunnel](https://github.com/localtunnel/localtunnel) to easily give us a way to send webhooks from GitHub to our server. 

1. Go to the GitHub repository where you want to utilize webhooks. 
2. Settings
3. Webhooks
4. Create a webhook
   - Supply the URL outputted from `localtunnel` for `Payload URL` and append `/github-webhook/` to it.
     
     It should look something like: `https://weak-beans-speak.loca.lt/github-webhook/`
   - Change `Content type` to `application/json`
   - Adjust `events` if necessary. `push` is usually enough for our needs.

> [!WARNING]
> There is a difference between `<url>/github-webhook/` and `<url>/github-webhook`. If you use `<url>/github-webhook`, you'll likely run into 302 status codes. See this [post](https://stackoverflow.com/a/51545557) on Stack Overflow.

Similar tools exist that could also be used, such as [ngrok](https://ngrok.com/). Some others can be found [here](https://free-for.dev/#/?id=tunneling-webrtc-web-socket-servers-and-other-routers).