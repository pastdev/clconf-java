# clconf

# Development

## Code Style
Code style is enforced using [checkstyle](https://checkstyle.org/) at build time.  The style is defined in [pastdev_checks.xml](pastdev_checks.xml), and is enforced during the build.  Any violations will fail the build.

### Eclipse Integration
To help prevent style violations before they start, you should install the [Eclipse-CS plugin](http://checkstyle.org/eclipse-cs/#!/).  Once installed you will need to import the style configuration, and create a formatter from it.

#### Import Checkstyle Configuration
There is more than one way to [configure](https://checkstyle.org/eclipse-cs/#!/configtypes) the plugin.  Given that this same configuration will likely be used by many projects, it probably makes the most sense to import into an _Internal Configuration_:

1) Window -> Preferences
2) Checkstyle -> New...
   * Type: _Internal Configuration_
   * Name: Pastdev Checks
   * Import...: select the pastdev\_checks.xml from the project
   * OK


#### Create Formatter
From package explorer, right-click your project:

1) Checkstyle -> Create Formatter-Profile

This generates a new formatter named _eclipse-cs <projectName>_. You will need to a couple minor customizations to avoid conflicts between the formatter and the validation:

1) Window -> Preferences
2) Java -> Code Style -> Formatter
   * Active Profile: _eclipse-cs <projectName>_, Edit...
     * Line Wrapping
       * Never join already wrapped lines: checked
       * Select Annotations -> Element-value pairs
         * Line wrapping policy: Wrap where necessary
   * Apply
   * OK
3) Apply
4) Java -> Code Style -> Organize Imports
   * Remove all items other than `java` and `javax`
   * Apply
5) Apply and Close

Then, to enable the formatter, right-click your project:

1) Checkstyle -> Activate Checkstyle

