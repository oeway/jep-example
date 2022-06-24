# Using CPython via Jep


## Development
```
conda create -n jep python=3.8 -y
conda activate jep
pip install -r requirements.txt
```

For the java project to work, you need to make sure jep is visible to java:
```
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$(python -c "import site; print(''.join(site.getsitepackages()))")/jep
```

Build the project
```
mvn
```

Run the example
```
mvn exec:java -Dexec.mainClass=Simple
```