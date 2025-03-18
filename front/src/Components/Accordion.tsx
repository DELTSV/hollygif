import Card from "./Card.tsx";
import {ReactNode, useRef, useState} from "react";
import {ChevronDown} from "react-feather";

interface AccordionProps {
    title: string,
    children: ReactNode,
    className?: string,
}

export default function Accordion(props: AccordionProps) {
    const [isOpen, setIsOpen] = useState<boolean>(false);
    const contentHeight = useRef<HTMLDivElement>(null);
    return (
        <Card className={props.className}>
            <div className={"border-black border-b border-solid overflow-hidden"}>
                <button className={"w-full text-left py-2 px-2 flex items-center justify-between font-medium text-xl bg-transparent border-none cursor-pointer"} onClick={() => setIsOpen(prev => !prev)}>
                    <p>{props.title}</p>
                    <ChevronDown className={isOpen ? "rotate-180" : ""}/>
                </button>
                <div className={"px-4 transition-all"} ref={contentHeight} style={isOpen ? {height: contentHeight.current?.scrollHeight + "px"} : {height: 0}}>
                    {props.children}
                </div>
            </div>
        </Card>
    )
}