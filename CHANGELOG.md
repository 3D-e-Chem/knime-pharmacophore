# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).
The file is formatted as described on http://keepachangelog.com/.

## [Unreleased]

## [1.0.5] - 2023-11-21

### Changes

- Requires KNIME 5.1

## [1.0.4] 2019-09-24

### Fixed

- Pharmacophore from molecule node parsed SDF incorrectly ([#12](https://github.com/3D-e-Chem/knime-pharmacophore/issues/12))

## [1.0.3] 2019-06-27

### Changed

- Compatible with KNIME 4 [#10](https://github.com/3D-e-Chem/knime-pharmacophore/issues/10)

## [1.0.2] 2018-07-05

### Fixed

- PharmacophorePoint#toArray method uses current locale [#9]

## [1.0.1] 2017-11-21

### Fixed

- Transformation matrix performs mirroring [#8]

## [1.0.0] 2017-09-26

### Added

- Node extract points from pharmacophore and node that does the reverse [#2]
- Nodes to read/write phar formatted files
- Nodes to convert between pharmacophore and molecule (sdf/mol) [#3]
- Node to align one pharmacophore to another [#4]

### Fixed

- RMSD very high for a good fit [#6]
