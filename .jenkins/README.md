# Jenkins

This guide covers how to set up Jenkins locally.

## Prequisites

- [Docker](https://www.docker.com/get-started/)
- [Just](https://just.systems/man/en/introduction.html)
  
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

There's some configuration involved to start up Jenkins. We use a `.env` and secrets in the `secrets/` directory to contain that configuration.

Use the following commands to create the necessary files automatically:

```sh
cp .env.example .env
just init-files
```

### `.env` Configuration

Some variables already have provided values. These values can be modified to better suit your configuration if necessary.

The `JENKINS_API_TOKEN` will be necessary later but does not need to be specified when initializing Jenkins.

### Secrets Configuration

Provide values for the variables in the `secrets/secrets.properties` file.

#### GitHub Secrets

In order for Jenkins to retrieve our code or interact with GitHub, the server will send API requests to GitHub. GitHub has [limits](https://docs.github.com/en/rest/using-the-rest-api/rate-limits-for-the-rest-api?apiVersion=2022-11-28#about-primary-rate-limits) for unauthenticated and authenticated users. Notably, unauthenticated requests are limited to only 60 requests per hour, which can impact the running of our pipelines if we remain unauthenticated. 

To obtain a higher limit, we can use [personal access tokens (PATs)](https://github.com/settings/personal-access-tokens) or [GitHub Apps](https://docs.github.com/en/apps) from GitHub. A guide on [creating and using GitHub Apps with Jenkins](https://github.com/jenkinsci/github-branch-source-plugin/blob/master/docs/github-app.adoc) has already been written by Jenkins themselves. 

> [!NOTE]
> If you're testing locally and don't have your Jenkins server accessible through the internet, you won't be able to supply a webhook URL in your GitHub App or repository webhook configuration. An alternative solution would be to use tunneling. See [Tunneling](#tunneling) for more information.

With our initialization process, we use [JCasC](https://plugins.jenkins.io/configuration-as-code/) to provide these credentials to our Jenkins server on creation. For our secret configuration, we need the **App ID** and a private key. The **App ID** can be found on the **General** page of the App. Jenkins' guide goes over the private key process and provides a command to convert a GitHub key to a compatible key for Jenkins. That command is provided in our `justfile` to easily handle that conversion.

Usage:

```sh
just convert-key <in> <out>

# Example
just convert-key ./secrets/github.pem ./secrets/jenkins.pem 
```

For our configuration, the key for Jenkins should be named `github_app_team_key.pem` and in the `secrets/` directory.

### Creation

Once configuration has been set, the Jenkins server can be created:

```sh
just init-jenkins
```

This will create the Jenkins server with the necessary plugins installed, custom configurations set, and jobs created. After running this command, there will be additional instructions output to the terminal that should be completed to finish the set up, which will briefly be discussed in this guide. If you want to see those instructions at any point, use `just instructions`.

### Jenkins API Token and Jenkins CLI

Using the Jenkins CLI allows for remote use, scriptability, and interaction with the Jenkins server through the terminal. In order to send commands to a Jenkins server, you need an API token. A password could also be used rather than an API token, but for security management, it's recommended to use an API token as multiple can be made and deleted if necessary.

To create an API token for a user, go to `<jenkins-url>/user/<username>/security` in your browser where `<jenkins-url>` is the URL to the Jenkins server and `<username>` is the name of the user, e.g. `http://localhost:8080/user/admin/security`. After creating the token, copy the token and set the `JENKINS_API_TOKEN` variable in the `.env` file to the token.

If you haven't downloaded the CLI, use `just dl`. Once you have the CLI, you should be able to use the Jenkins CLI through `just j`.

```sh
just j list-jobs
just j version
```

Use `just help` to view available commands, or in your browser, go to `<jenkins-url>/cli`. Substitute `<jenkins-url>` with the URL of your Jenkins server. 

At this point, Jenkins and your local environment should be fully set up. To understand how to manage configuration updates, see [Updating Configuration](#updating-configuration).

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

## Updating Configuration

To manage Jenkins configuration, we use the [JCasC Plugin](https://github.com/jenkinsci/configuration-as-code-plugin) and the files are located [here](./configs/). The full configuration that Jenkins uses is actually a lot larger, but ours only contains our custom configurations that will override the default values.

If you make updates to the configuration, you'll likely first make updates through the UI. If you want to save those changes to the config files, you can view the current state of the JCasC file on the Jenkins server by using the following command:

```sh
just view-config
just v # Shorthand
```

Copy the modified parts and update `configs/dynamic.yml` with those changes.

### Static and Dynamic Configuration

We have two configuration files `static.yml` and `dynamic.yml`. 
- `static.yml` contains properties that **CANNOT** be remotely applied to the server via [remote application](#remote-application). 
- `dynamic` contains properties that **CAN** be remotely applied to the server via [remote application](#remote-application).

How we identify which properties belong to which file is simply trial and error. By default, you should make your updates to `dynamic.yml`. If you can remotely apply it to the server, then it should stay there. Otherwise, move it to `static.yml`.

### Sending and Reloading Configuration

There's two ways to update the Jenkins' configuration through the terminal. If we're on the same machine as the running Jenkins instance, we can copy the files locally to the docker container. Otherwise, we can send the config to the server remotely.

#### Local application

You can copy the local configuration files to Jenkin's docker container and reload the configuration.

```sh
just send-local-config
just reload-config

# One liners
just send-local-config reload-config
just s r
```

#### Remote application

We can send the server a configuration to update its configuration and reload it.

```sh
just apply-config
just a # Shorthand
```

> [!NOTE]
> This will only send and apply the `configs/dynamic.yml` file. If you find yourself running into errors involving conflicting values, it is likely a property that should belong to `configs/static.yml` and should be handled through [local application](#local-application). See [Static and Dynamic Configuration](#static-and-dynamic-configuration) for more information.

### Job Configuration

We use the [Job DSL Plugin](https://github.com/jenkinsci/job-dsl-plugin) to create jobs. When making changes to jobs through the UI, it's important to note that there is no way to obtain the job configuration in Job DSL API syntax. You have to manually write those changes in `configs/dynamic.yml`. Refer to the documentation on the Job DSL API by going to the [general site](https://jenkinsci.github.io/job-dsl-plugin) or for more specific information tailored to your plugins, `<jenkins-url>/plugin/job-dsl/api-viewer/index.html`. Additional information on working with Job DSL and JCasC together can be found [here](https://github.com/jenkinsci/job-dsl-plugin/wiki/JCasC).

After updating `configs/dynamic.yml`, ensure you update and reload the configuration on the Jenkins server:

```sh
just s r
```

## Tunneling

To test webhook interactions with our pipeline on our local environment, we can use a tunnel to redirect payloads to our server without having to expose our own ports.

To enable this, ensure the Jenkins server is running and then run:

```sh
just tunnel # Outputs a URL where we can access our Jenkins server
```

This uses [localtunnel](https://github.com/localtunnel/localtunnel) to easily give us a way to send webhooks from GitHub to our server. Similar tools exist that could also be used, such as [ngrok](https://ngrok.com/). Some others can be found [here](https://free-for.dev/#/?id=tunneling-webrtc-web-socket-servers-and-other-routers).

Once you have the URL, your webhook URL that you enter in GitHub will look something like `https://weak-beans-speak.loca.lt/github-webhook/`.

> [!WARNING]
> There is a difference between `<url>/github-webhook/` and `<url>/github-webhook`. If you use `<url>/github-webhook`, you'll likely run into 302 status codes. See this [post](https://stackoverflow.com/a/51545557) on Stack Overflow.

