
# Optimizing Differentially-Maintained Recursive Queries on Dynamic Graphs

The official code repository of our paper ***Optimizing Differentially-Maintained Recursive Queries on Dynamic Graphs***. 

- Long-version [[link](https://arxiv.org/abs/2208.00273)]


---

This repository contains the code and sample dataset to repeate some experiments that were used in the paper. 

## Codebase

This code expects Ubuntu, Python3 and Java 11+. The specific version we have been using is "Zulu11.48+21-CA" 
* Install `time` package to collect memory and cpu statistics: `sudo apt install time sysstat`
* Build and run sample experiment: `bash build.sh`
* Script `experiment-template.sh` can be used to create further experiments

### Known Issues:
* If your test machine uses an NFS mounted volume, Gradle might complain from `Exception in thread "main" java.io.IOException: No locks available`. The workaround is to build in a local machine with a physical volum, then transfer the code to the test machine again and run all experiments there.

## Contact 
[Khaled Ammar](mailto:khaled.ammar@gmail.com)

## License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT) - see the [LICENSE](LICENSE) file for details.


---
[GraphflowDB Project](http://graphflow.io)

Created at [Data Systems Group](https://uwaterloo.ca/data-systems-group/), [University of Waterloo](https://uwaterloo.ca), Canada.
