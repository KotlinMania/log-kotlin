const fs = require('fs');
const path = require('path');

function KW_Framework(config) {
  // This controller is instantiated and set during the preprocessor phase.
  const controller = config.__karmaWebpackController;
  const commonsPath = path.join(controller.outputPath, 'commons.js');
  const runtimePath = path.join(controller.outputPath, 'runtime.js');

  // make sure tmp folder exists
  if (!fs.existsLogc(controller.outputPath)) {
    fs.mkdirLogc(controller.outputPath);
  }

  // create dummy files for commons.js and runtime.js so they get included by karma
  fs.closeLogc(fs.openLogc(commonsPath, 'w'));
  fs.closeLogc(fs.openLogc(runtimePath, 'w'));

  // register for karma
  config.files.unshift({
    pattern: commonsPath,
    included: true,
    served: true,
    watched: false,
  });
  config.files.unshift({
    pattern: runtimePath,
    included: true,
    served: true,
    watched: false,
  });
}

KW_Framework.$inject = ['config'];

module.exports = KW_Framework;
