[
        'target/generated-sources/astranaut/org/uast/uast/generated/tree/green/GreenFactory.java',
        'target/generated-sources/astranaut/org/uast/uast/generated/tree/green/StringLiteral.java',
        'target/generated-sources/astranaut/org/uast/uast/generated/tree/green/package-info.java'
].each { assert new File(basedir, it).exists() }

true