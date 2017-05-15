# SoprIdées backend

[![Build Status](https://travis-ci.org/SopraSteriaGroup/initiatives_backend_ideas.svg?branch=master)](https://travis-ci.org/SopraSteriaGroup/initiatives_backend_ideas)
![license](https://img.shields.io/badge/license-MIT-blue.svg)

Application exposant une [API REST](https://soprasteriagroup.github.io/initiatives_backend_ideas/) pour gérer les SoprIdées.

## Installation

### Pré-requis

Le démarrage de l'application nécessite les outils suivants :
* [Git](https://git-scm.com/book/fr/v1/D%C3%A9marrage-rapide-Installation-de-Git) pour la récupération des sources
* [Gradle](https://gradle.org/) pour gérer le cycle de vie de l'application (compilation, build, test, ...)
* [MongoDB](https://www.mongodb.com/download-center#community) comme base de données
* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) comme runtime
* [Kotlin](https://kotlinlang.org/) comme langage de programmation

Le développement sur l'application nécessite un IDE supportant [Kotlin](https://kotlinlang.org/).
[Intellij IDEA](https://www.jetbrains.com/idea/) est l'IDE conseillé pour les développements *Kotlin*.

### Démarrage

Le JDK8 doit être installé ainsi que Kotlin.

Après avoir installé *Git* et demandé les autorisations sur le repository. Se placer dans le répertoire qui contiendra les sources de 
l'application saisir la commande ``git clone https://github.com/SopraSteriaGroup/initiatives_backend_ideas.git``.

La CLI devrait afficher le message : *Checking connectivity... done.*

Démarrer *MongoDB* avec la commande : ``mongod``

Après avoir installé Gradle, à la racine du projet, exécutez la commande : ``gradle bootRun``.
 
L'application devrait démarrer en affichant dans la CLI un message contenant : ``Started InitiativesIdeasApplicationKt in X seconds`