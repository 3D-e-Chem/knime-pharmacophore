[KNIME](https://www.knime.com) plugin with nodes to convert and align pharmacophores.

[![Travis-CI Build Status](https://travis-ci.org/3D-e-Chem/knime-pharmacophore.svg?branch=master)](https://travis-ci.org/3D-e-Chem/knime-pharmacophore)
[![AppVeyor Build status](https://ci.appveyor.com/api/projects/status/0d27c4nhkjopy69r/branch/master?svg=true)](https://ci.appveyor.com/project/3D-e-Chem/knime-pharmacophore/branch/master)
[![SonarCloud Gate](https://sonarcloud.io/api/badges/gate?key=nl.esciencecenter.e3dchem.knime.pharmacophore:nl.esciencecenter.e3dchem.knime.pharmacophore)](https://sonarcloud.io/dashboard?id=nl.esciencecenter.e3dchem.knime.pharmacophore:nl.esciencecenter.e3dchem.knime.pharmacophore)
[![SonarCloud Coverage](https://sonarcloud.io/api/badges/measure?key=nl.esciencecenter.e3dchem.knime.pharmacophore:nl.esciencecenter.e3dchem.knime.pharmacophore&metric=coverage)](https://sonarcloud.io/component_measures/domain/Coverage?id=nl.esciencecenter.e3dchem.knime.pharmacophore:nl.esciencecenter.e3dchem.knime.pharmacophore)
[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.997333.svg)](https://doi.org/10.5281/zenodo.997333)

A pharmacophore is an abstract description of molecular features that are necessary for molecular recognition of a ligand by a biological macromolecule.
Nodes in this plugin allow for converting pharmacophores, from and to molecules, by mapping elements to pharmacophore type and, reading from or writing to the phar file format used by the [Silicos IT align-it tool](http://silicos-it.be.s3-website-eu-west-1.amazonaws.com/software/align-it/1.0.4/align-it.html#pharmacophores).
This plugin adds the Pharmacophore (Phar) data type to KNIME, allowing nodes to read/write/manipulate pharmacophores inside KNIME like the [Silicos-it align-it](https://github.com/3D-e-Chem/knime-silicos-it), [Kripo pharmacophore retrieval](https://github.com/3D-e-Chem/knime-kripodb) and [molviewer pharmacophore viewer](https://github.com/3D-e-Chem/knime-molviewer) nodes.

This project uses [Eclipse Tycho](https://www.eclipse.org/tycho/) to perform build steps.

# Installation

Requirements:

* KNIME, https://www.knime.org, version 3.3 or higher

Steps to get the Pharmacophore KNIME nodes inside KNIME:

1. Goto Help > Install new software ... menu
2. Press add button
3. Fill text fields with `https://3d-e-chem.github.io/updates`
4. Select --all sites-- in `work with` pulldown
5. Select the `Pharmacophore KNIME nodes`
6. Install software
7. Restart KNIME

# Usage

1. Create a new KNIME workflow.
2. Find node in Node navigator panel.
3. Drag node to workflow canvas.

# Examples

The `examples/` folder contains example KNIME workflows.

# Build

```
mvn verify
```

An Eclipse update site will be made in `p2/target/repository` directory.
The update site can be used to perform a local installation.

## Continuous Integration

Configuration files to run Continuous Integration builds on Linux (Travis-CI), OS X (Travis-CI) and Windows (AppVeyor) are present.

See `./.travis.yml` file how to trigger a Travis-CI build for every push or pull request.
Also see `./.travis.yml` file how to upload coverage to https://www.codacy.com .

See `./appveyor.yml` file how to run on https://www.appveyor.com .

# Development

Steps to get development environment setup:

1. Download `Eclipse for RCP and RAP Developers` from http://www.eclipse.org/downloads/eclipse-packages
2. Start Eclipse
2. Import a clone of this repository as an Existing Maven project. During import the Tycho Eclipse providers must be installed
3. Open platform/platform.target file
4. Press `Set as target platform` button and wait for the rebuild to complete

When switching to an other project make sure to update the target platform. The target platform can be reset by going to Window > Preferences > Plug-in Development > Target Platform, and select Running Platform.

Adding a bundle to Require-Bundle field in */META-INF/MANIFEST.MF files will also require the bundle to be added to locations in platform/platform.target file followed by a reload, to allow Eclipse to resolve the bundle and for Tycho/Maven to resolve the bundle if it is in a new update site.

## Tests

Tests for the node are in `tests/src` directory.
Tests can be executed with `mvn verify`, they will be run in a separate KNIME environment.
Test results will be written to `test/target/surefire-reports` directory.
Code coverage reports (html+xml) can be found in the `tests/target/jacoco/report/` directory.

### Unit tests

Unit tests written in Junit4 format can be put in `tests/src/java`.

### Workflow tests

See https://github.com/3D-e-Chem/knime-testflow#3-add-test-workflow

## Speed up builds

Running mvn commands can take a long time as Tycho fetches indices of all p2 update sites.
This can be skipped by running maven offline using `mvn -o`.

# New release

1. Update versions in pom files with `mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=<version>-SNAPSHOT` command.
2. Create package with `mvn package`, will create update site in `p2/target/repository`
3. Run tests with `mvn verify`
4. Optionally, test node by installing it in KNIME from a local update site
5. Append new release to an update site
  1. Make clone of an update site repo
  2. Append release to the update site with `mvn install -Dtarget.update.site=<path to update site>`
6. Commit and push changes in this repo and update site repo.
7. Create a Github release
8. Update Zenodo entry
  1. Correct authors
  2. Correct license
9. Make nodes available to 3D-e-Chem KNIME feature by following steps at https://github.com/3D-e-Chem/knime-node-collection#new-release

