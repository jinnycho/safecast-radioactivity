
<!-- TABLE OF CONTENTS -->

## About the Project
An interactive map to show the severity of radiation around the world.

<img src="https://user-images.githubusercontent.com/45345735/61601909-91a78680-ac05-11e9-8754-913b6941ae4d.gif" width="500">


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
- Unzip the dataset.
- Split the dataset to a reasonable size (~2GB) using `split -b 2000m split_me.csv`.
- If necessary, update the path to that csv file [here](https://github.com/jinnycho/safecast-radioactivity/blob/905f7cd8be80de5a8ddab5d760e17191a6c52a9a/src/main/scala/SafecastClustering.scala#L135).
- In the root directory, run `sbt assembly` so everything's compiled to run spark jobs.
- Then run `spark-submit target/scala-2.11/SafecastRadioactivityMap-assembly-1.0.jar`

### 2. To see the map
- Run `npm start`
- Open `http://localhost:3000/`

## More details
- Please visit to https://jinnycho.github.io/blog/safecast.
