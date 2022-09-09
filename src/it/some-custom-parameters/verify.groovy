[
    'target/generated-sources/astranaut/org/uast/gen/green/GreenFactory.java',
    'target/generated-sources/astranaut/org/uast/gen/green/Identifier.java',
    'target/generated-sources/astranaut/org/uast/gen/green/Expression.java',
    'target/generated-sources/astranaut/org/uast/gen/green/BinaryExpression.java',
    'target/generated-sources/astranaut/org/uast/gen/green/Variable.java',
    'target/generated-sources/astranaut/org/uast/gen/green/Addition.java',
    'target/generated-sources/astranaut/org/uast/gen/green/Subtraction.java',
    'target/generated-sources/astranaut/org/uast/gen/green/package-info.java',

    'target/generated-sources/astranaut/org/uast/gen/js/JsAdapter.java',
    'target/generated-sources/astranaut/org/uast/gen/js/JsFactory.java',
    'target/generated-sources/astranaut/org/uast/gen/js/rules',
    'target/generated-sources/astranaut/org/uast/gen/js/package-info.java',

    'target/generated-sources/astranaut/org/uast/gen/python/This.java',
    'target/generated-sources/astranaut/org/uast/gen/python/PythonAdapter.java',
    'target/generated-sources/astranaut/org/uast/gen/python/PythonFactory.java',
    'target/generated-sources/astranaut/org/uast/gen/js/rules',
    'target/generated-sources/astranaut/org/uast/gen/js/package-info.java'
].each { assert new File(basedir, it).exists() }

true