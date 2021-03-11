
Array.from(document.getElementById("categories")
.children).reduce((acc,cur)=>{
    //get main category
    let category = cur.querySelector("h3").innerText

    //now get subcategories and put them in the accumulator object
    acc[category] = Array.from(cur.children[1].children).map(c => c.innerText)

    console.log(acc)
},{})