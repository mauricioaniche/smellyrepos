# Smelly Repos

This simple tool detects methods in repositories that are, let's say, _a bit strange_.
Suppose a repository `InvoiceRepository`. For each method in this repository, the tool
considers it "right method" if one of the following rules is true:

- Return type matches class name (e.g. Invoice), 
is primitive, is an enum or is a subtype of the type.  		

- Receive a parameter from the type (e.g. void save(Invoice i))

## Executing the tool

You can download the jar at our releases page (https://github.com/mauricioaniche/smellyrepos/releases).

The tool receives 3 parameters:

```
java -jar smellyrepos.jar <project path> <regex> <output file>
```

The regex should explain how the nomenclature of your repositories work. It should also
explicit delimit the repository name, so that we can extract it. Examples are: 
(.*)dao, all(.*)s, etc.

Example:

```
java -jar smellyrepos.jar 
	/Users/mauricioaniche/workspace/gnarus/ 
	"(.*)dao" 
	result.txt
``` 

## References

- Aniche, Maurício F., Gustavo A. Oliva, and Marco A. Gerosa. "Are the Methods in Your Data Access Objects (DAOs) in the Right Place? A Preliminary Study.". 6th Workshop on Managing Technical Debt, in conjunction with ICSME2014, Canada.

- Calçado, Phil. "How to write a Repository". http://philcalcado.com/2010/12/23/how_to_write_a_repository.html.

## License

This software is licensed under the Apache 2.0 License.
