[
    'src/main/java/org/example/org/uast/uast/generated/tree/green/GreenFactory.java',
    'src/main/java/org/example/org/uast/uast/generated/tree/green/Identifier.java',
    'src/main/java/org/example/org/uast/uast/generated/tree/green/Expression.java',
    'src/main/java/org/example/org/uast/uast/generated/tree/green/BinaryExpression.java',
    'src/main/java/org/example/org/uast/uast/generated/tree/green/Variable.java',
    'src/main/java/org/example/org/uast/uast/generated/tree/green/Addition.java',
    'src/main/java/org/example/org/uast/uast/generated/tree/green/Subtraction.java',
    'src/main/java/org/example/org/uast/uast/generated/tree/green/package-info.java',

    'src/main/java/org/example/org/uast/uast/generated/tree/js/JsAdapter.java',
    'src/main/java/org/example/org/uast/uast/generated/tree/js/JsFactory.java',
    'src/main/java/org/example/org/uast/uast/generated/tree/js/rules',
    'src/main/java/org/example/org/uast/uast/generated/tree/js/package-info.java',

    'src/main/java/org/example/org/uast/uast/generated/tree/python/This.java',
    'src/main/java/org/example/org/uast/uast/generated/tree/python/PythonAdapter.java',
    'src/main/java/org/example/org/uast/uast/generated/tree/python/PythonFactory.java',
    'src/main/java/org/example/org/uast/uast/generated/tree/js/rules',
    'src/main/java/org/example/org/uast/uast/generated/tree/js/package-info.java'
].each { assert new File(basedir, it).exists() }

true