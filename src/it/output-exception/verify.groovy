def log = new File(basedir, 'build.log')
assert log.text.contains('Specified target directory for generation is inside existing source root')