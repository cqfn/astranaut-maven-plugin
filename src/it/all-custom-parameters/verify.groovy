[
    'src/main/java/org/uast/gen/green/GreenFactory.java',
    'src/main/java/org/uast/gen/green/Identifier.java',
    'src/main/java/org/uast/gen/green/Expression.java',
    'src/main/java/org/uast/gen/green/BinaryExpression.java',
    'src/main/java/org/uast/gen/green/Variable.java',
    'src/main/java/org/uast/gen/green/Addition.java',
    'src/main/java/org/uast/gen/green/Subtraction.java',
    'src/main/java/org/uast/gen/green/package-info.java',

    'src/main/java/org/uast/gen/js/JsAdapter.java',
    'src/main/java/org/uast/gen/js/JsFactory.java',
    'src/main/java/org/uast/gen/js/rules',
    'src/main/java/org/uast/gen/js/package-info.java',

    'src/main/java/org/uast/gen/python/This.java',
    'src/main/java/org/uast/gen/python/PythonAdapter.java',
    'src/main/java/org/uast/gen/python/PythonFactory.java',
    'src/main/java/org/uast/gen/js/rules',
    'src/main/java/org/uast/gen/js/package-info.java'
].each { assert new File(basedir, it).exists() }

true