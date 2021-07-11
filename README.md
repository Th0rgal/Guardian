<h1 align="center">
  <br>
  <img src="./src/design/logo.svg" alt="guardian logo" width="512">
  <br>
</h1>

<h4 align="center">🛡️️ Source code of the Guardian Anti Cheat, made with love in Java.</h4>

<p align="center">
    <a href="https://discord.gg/4Qk5kBT9UX" alt="discord">
        <img src="https://img.shields.io/discord/480068531132563476?label=chat&logo=discord"/>
    </a>
    <a href="https://th0rgal.gitbook.io/guardian/" alt="Docs (gitbook)">
        <img src="https://img.shields.io/badge/docs-gitbook-brightgreen"/>
    </a>
</p>

### Getting started
Installing Guardian is a fairly straight forward process: drop Guardian.jar and ProtocolLib 
to your /plugins/ folder and restart your server. [Here is](https://th0rgal.gitbook.io/guardian/) 
the documentation for more details on how to use the plugin.

### Current Features
| Movements     | Combat        | Render        | Miscellaneous |
| ------------- | ------------- | ------------- | ------------- |
| Flight        | HighCPS       | HealthBar     | AutoArmor     |
| Speed         | Reach         |               | Inv Walking   |

### What's next?
One of the next steps for Guardian is to offer a check for killaura. The aim is to get 
something that is silent, low on performance and that will block most abnormal behaviour.
One strategy could be to analyse mouse movements to detect the killaura fingerprint.
Other additions are in the pipeline: for example, a reporting system integrated into the 
plugin and a means of adapting the plugin to the performance of the server (by reducing 
greedy checks or disabling those that require optimal performance). Don't hesitate to
suggest your ideas on Github.