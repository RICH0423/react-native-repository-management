# React-Native Repository Management
Repository Management is a store module for remote RESTful api processing

## Setup

* Install Module

```bash
npm install react-native-repository-management --save
```

* Linking Module

```bash
rnpm link react-native-repository-management
```

## Usage

1. Import 

```javascript
import RepositoryMgmt from '../../../node_modules/react-native-repository-management';
```

2. Invoke 'callAPI' method

```javascript
  var data = new Object();
  data["url"] = 'http://10.0.2.2:8088/SpringBoot/api/todo';
  data['method'] = 'GET';

  RepositoryMgmt.callAPI(data,
    (response) => {
      console.log("API response: " + response);
      
      if (response !== null) {
      this.setState({notes:JSON.parse(response)})
      }
    },
    (msg) => {
      console.log('Load all notes error' + msg);
    });

```
