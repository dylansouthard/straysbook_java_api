const fs = require('fs');
const path = require('path');

const errorsJsonPath = path.join(__dirname, '../src/main/resources/errors.json');
const outputPath = path.join(__dirname, '../src/main/java/info/dylansouthard/StraysBookAPI/errors/ErrorCodes.java');

const errors = JSON.parse(fs.readFileSync(errorsJsonPath, 'utf8'));

const keys = Object.keys(errors);

let javaCode = `public interface ErrorCodes {\n`;

keys.forEach(key => {
    javaCode += `    String ${key} = "${key}";\n`;
});

javaCode += `}\n`;

fs.writeFileSync(outputPath, javaCode);
console.log('✅ ErrorCodes.java generated successfully!');