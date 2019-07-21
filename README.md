
<!-- TABLE OF CONTENTS -->

## About the Project
An interactive map to show the severity of radiation around the world.

<img src="https://user-images.githubusercontent.com/45345735/61595900-d6fa9280-abca-11e9-9166-c05357a65ab9.gif" width="500">


### Built With
- Data Processing
    - Scala
    - Spark SQL
    - Spark Mllib
    - sbt
- Visualization
    - Mapbox gl-js
    - D3
    - JavaScript
    - Node.js
    - Express

## Getting Started

### Prerequisites
- Spark v2.2.0
- Scala v2.11.8
- npm v6
- node v10

## Usage
### 1. To update dataset
- Install csv dataset from [Safecast Org](https://blog.safecast.org/downloads/).
- Unzip the dataset
- Split the dataset to a reasonable size (~2GB) using `split -b 2000m split_me.csv` 
