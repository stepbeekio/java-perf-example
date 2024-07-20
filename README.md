# Getting Started

## Purpose
This repository is intended to provide an easy way to test a fairly trivial Spring Boot application in resource constrained 
environments. As a part of this, a script is included below to run the application with a variety of CPU memory/combinations.

No effort has been given to visualization here, but one might output vegeta binary files and graph from those.

### Result

This was tested on a desktop running Fedora 38. The machine had an AMD Ryzen 5950x CPU, 32GiB of 3600 MHz DDR4 RAM and a Samsung 970 Pro NVMe SSD.  

This does not accurately reflect the environment on most cloud providers. Running this test setup on an EC2 instance would 
be an interesting way to learn more about how virtualization and lower performing CPUs impact this score.

You can see an example run in the example-results.txt file

### Startup
The application struggles to startup in a reasonable amount of time for low CPU:
  * 0.25CPU/1GiB RAM started in 22s
  * 0.5CPU/1GiB RAM started in 9.1s
  * 1CPU/1GiB RAM started in 4.3s
  * 2CPU/1GiB RAM started in 2.2s
  * beyond this point the gains were marginal.

### Performance
Given the trivial nature of the functionality in this application, I'm choosing 100ms as the target p99 latency.

For the first case of 8 concurrent workers, the performance of the application scales dramatically with CPU up to around 2 CPU then tails off
  * 0.25 CPU/1GiB RAM -> 55TPS with p99 latency of 615ms 
  * 0.5 CPU/1GiB RAM -> 139TPS with p99 latency of 207ms 
  * 1CPU/1GiB RAM -> 359TPS with p99 latency of 91ms 
  * 2 CPU/2GiB RAM -> 768TPS with p99 latency of 60ms 
  * beyond this point the gain was marginal

This means that to serve 8 concurrent users with reasonable latency, we'd need to allocate 1CPU. 

For the case of 16 concurrent workers:
* 0.25 CPU/1GiB RAM -> 25TPS with p99 latency of 7,974ms
* 0.5 CPU/1GiB RAM -> 120TPS with p99 latency of 491ms
* 1CPU/1GiB RAM -> 319TPS with p99 latency of 192ms
* 2CPU/2GiB RAM -> 751TPS with p99 latency of 118ms
* 4CPU/4GiB RAM -> 826TPS with p99 latency of 120ms
* beyond this point the gain was marginal

This means that to serve 16 concurrent users with reasonable latency, we'd need to allocate 2CPU.



### Conclusions

The JVM has a high fixed cost that results in low performance in resource constrained environments. It strikes me that 
2CPU & 2GiB of RAM is where good performance began and anything beyond that was either bottlenecked by IO or enhanced throughput in the case 
of high worker counts.

This lines up roughly with the JVM intrinsic cutoffs for some optimization - e.g. G1GC by default.

# Note for reproduction

## Running the application

The purpose of this is to test java running in a docker container with limited resources.

While working locally, you can run `./gradlew bootRun --info` and the application will start up 
the postgresql database using docker compose and listen on `http://localhost:8080`

You'll need to have java, gradle etc. installed. You'll also need vegeta

## Building the application

You can build the docker image by running 

```bash
./gradlew bootBuildImage --info
```

### Running the benchmark
You can run the benchmark with the following scripts

```bash
./gradlew bootBuildImage --info

cd vegeta 
./create-targets.sh

# Create a file for this run
timestamp=$(date +"%Y-%m-%d_%H-%M-%S")

result_file="results/$timestamp"
mkdir -p results
touch $result_file

resources=("vvlow 0.25 1g" "vlow 0.5 1g" "low 1 1g" "med 2 2g" "high 4 4g" "vhigh 8 8g" "vvhigh 16 8g")
users=(4 8 16 32 64 128)
for concurrent_users in ${users[@]}; do
  echo "-----------------"
  echo "STARTING SUITE FOR $concurrent_users CONCURRENT USERS" | tee -a $result_file
  for str in ${resources[@]}; do 
    name=$(echo $str | cut -d " " -f 1)
    cpu=$(echo $str | cut -d " " -f 2)
    mem=$(echo $str | cut -d " " -f 3)
    echo "---------"  | tee -a $result_file
    docker-compose down -v
    docker-compose up -d
    echo "waiting for everything to start...."
    sleep 5s
    docker stop example
    docker rm example
    echo "User count $concurrent_users. Running $name config with ${cpu} cpu and ${mem} memory" | tee -a $result_file
    echo "Starting container..."  | tee -a $result_file
    docker run -d --name example -p 8080:8080 -e "SPRING_PROFILES_ACTIVE=docker" --cpus $cpu --memory $mem docker.io/library/stepbeekio:0.0.1-SNAPSHOT
    sleep 30
    docker logs example 2>/dev/null | grep "Started JavaPerfExampleApplicationKt"  | tee -a $result_file
    echo "Running warmup..."  | tee -a $result_file
    vegeta attack -duration=30s -workers=1 --targets=targets.txt > /dev/null
    echo "Running benchmark..."  | tee -a $result_file
    vegeta attack -duration=60s -rate=0 -max-workers=$concurrent_users --targets=targets.txt | vegeta report --type=text | tee -a $result_file
    echo "-----"  | tee -a $result_file
  done
done

cd ..
```
